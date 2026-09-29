package com.library.ebook.service;

import com.library.ebook.dto.JournalEntryRequest;
import com.library.ebook.dto.JournalLineRequest;
import com.library.ebook.entity.*;
import com.library.ebook.exception.BusinessValidationException;
import com.library.ebook.exception.ResourceNotFoundException;
import com.library.ebook.exception.UnbalancedJournalException;
import com.library.ebook.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class AccountingService {

    private final ChartOfAccountRepository accountRepository;
    private final JournalRepository journalRepository;
    private final JournalEntryRepository entryRepository;
    private final JournalItemRepository itemRepository;

    public AccountingService(ChartOfAccountRepository accountRepository,
                             JournalRepository journalRepository,
                             JournalEntryRepository entryRepository,
                             JournalItemRepository itemRepository) {
        this.accountRepository = accountRepository;
        this.journalRepository = journalRepository;
        this.entryRepository = entryRepository;
        this.itemRepository = itemRepository;
    }

    // Chart of Accounts
    public List<ChartOfAccount> getAllAccounts() {
        return accountRepository.findAll();
    }

    public ChartOfAccount getAccountById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with ID: " + id));
    }

    public ChartOfAccount getAccountByCode(String code) {
        return accountRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with code: " + code));
    }

    public ChartOfAccount createAccount(ChartOfAccount account) {
        accountRepository.findByCode(account.getCode()).ifPresent(a -> {
            throw new BusinessValidationException("Account code " + account.getCode() + " already exists.");
        });
        return accountRepository.save(account);
    }

    // Journals
    public List<Journal> getAllJournals() {
        return journalRepository.findAll();
    }

    public Journal getJournalByCode(String code) {
        return journalRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Journal not found with code: " + code));
    }

    public List<JournalEntry> getAllEntries() {
        return entryRepository.findTop20ByOrderByEntryDateDescIdDesc();
    }

    public JournalEntry getEntryById(Long id) {
        return entryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Journal entry not found with ID: " + id));
    }

    public JournalEntry createJournalEntry(JournalEntryRequest request) {
        return postManualJournalEntry(request);
    }

    // Core Double-Entry Transaction Creator
    public JournalEntry postManualJournalEntry(JournalEntryRequest request) {
        Journal journal = journalRepository.findById(request.getJournalId())
                .orElseThrow(() -> new ResourceNotFoundException("Journal not found with ID: " + request.getJournalId()));

        if (request.getLines() == null || request.getLines().isEmpty()) {
            throw new BusinessValidationException("Journal entry must contain at least one line.");
        }

        BigDecimal sumDebit = BigDecimal.ZERO;
        BigDecimal sumCredit = BigDecimal.ZERO;

        for (JournalLineRequest line : request.getLines()) {
            sumDebit = sumDebit.add(line.getDebit() != null ? line.getDebit() : BigDecimal.ZERO);
            sumCredit = sumCredit.add(line.getCredit() != null ? line.getCredit() : BigDecimal.ZERO);
        }

        // Rule 5: TOTAL DEBIT = TOTAL CREDIT
        if (sumDebit.compareTo(sumCredit) != 0) {
            throw new UnbalancedJournalException("Journal transaction is unbalanced. Total Debit (" + sumDebit +
                    ") must equal Total Credit (" + sumCredit + ").");
        }

        String entryNumber = "JE-" + LocalDate.now().getYear() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        LocalDate date = request.getEntryDate() != null ? request.getEntryDate() : LocalDate.now();

        JournalEntry entry = new JournalEntry(entryNumber, journal, date, request.getReference(), request.getDescription());
        entry.setTotalDebit(sumDebit);
        entry.setTotalCredit(sumCredit);
        entry.setPosted(true);

        for (JournalLineRequest lineReq : request.getLines()) {
            ChartOfAccount acc = getAccountById(lineReq.getAccountId());
            JournalItem item = new JournalItem(acc, lineReq.getDebit(), lineReq.getCredit(), lineReq.getDescription());
            entry.addLine(item);
            applyAccountBalanceChange(acc, lineReq.getDebit(), lineReq.getCredit());
        }

        return entryRepository.save(entry);
    }

    /**
     * Programmatic helper to create balanced 2-legged or multi-legged journal entries
     */
    public JournalEntry postBalancedTransaction(JournalType journalType, String reference, String description,
                                                ChartOfAccount debitAccount, ChartOfAccount creditAccount, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessValidationException("Transaction amount must be greater than zero.");
        }

        List<Journal> journals = journalRepository.findByType(journalType);
        Journal journal = journals.isEmpty() ? journalRepository.findAll().get(0) : journals.get(0);

        String entryNumber = "JE-" + LocalDate.now().getYear() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        JournalEntry entry = new JournalEntry(entryNumber, journal, LocalDate.now(), reference, description);
        entry.setTotalDebit(amount);
        entry.setTotalCredit(amount);
        entry.setPosted(true);

        // Debit Line
        JournalItem debitItem = new JournalItem(debitAccount, amount, BigDecimal.ZERO, description);
        entry.addLine(debitItem);
        applyAccountBalanceChange(debitAccount, amount, BigDecimal.ZERO);

        // Credit Line
        JournalItem creditItem = new JournalItem(creditAccount, BigDecimal.ZERO, amount, description);
        entry.addLine(creditItem);
        applyAccountBalanceChange(creditAccount, BigDecimal.ZERO, amount);

        return entryRepository.save(entry);
    }

    private void applyAccountBalanceChange(ChartOfAccount account, BigDecimal debit, BigDecimal credit) {
        BigDecimal current = account.getBalance() != null ? account.getBalance() : BigDecimal.ZERO;
        BigDecimal d = debit != null ? debit : BigDecimal.ZERO;
        BigDecimal c = credit != null ? credit : BigDecimal.ZERO;

        // Standard accounting equation rules
        if (account.getType() == AccountType.ASSET || account.getType() == AccountType.EXPENSE) {
            account.setBalance(current.add(d).subtract(c));
        } else {
            // LIABILITY or INCOME
            account.setBalance(current.add(c).subtract(d));
        }
        accountRepository.save(account);
    }
}
