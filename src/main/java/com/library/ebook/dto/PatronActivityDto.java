package com.library.ebook.dto;

public class PatronActivityDto {
    private Long patronId;
    private String patronName;
    private String email;
    private String phone;
    private long activeCheckouts;
    private long returnedBooks;
    private long expiredAccess;
    private long activeHolds;
    private String membershipStatus;

    public PatronActivityDto() {}

    public Long getPatronId() { return patronId; }
    public void setPatronId(Long patronId) { this.patronId = patronId; }

    public String getPatronName() { return patronName; }
    public void setPatronName(String patronName) { this.patronName = patronName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public long getActiveCheckouts() { return activeCheckouts; }
    public void setActiveCheckouts(long activeCheckouts) { this.activeCheckouts = activeCheckouts; }

    public long getReturnedBooks() { return returnedBooks; }
    public void setReturnedBooks(long returnedBooks) { this.returnedBooks = returnedBooks; }

    public long getExpiredAccess() { return expiredAccess; }
    public void setExpiredAccess(long expiredAccess) { this.expiredAccess = expiredAccess; }

    public long getActiveHolds() { return activeHolds; }
    public void setActiveHolds(long activeHolds) { this.activeHolds = activeHolds; }

    public String getMembershipStatus() { return membershipStatus; }
    public void setMembershipStatus(String membershipStatus) { this.membershipStatus = membershipStatus; }
}
