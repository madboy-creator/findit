package com.findit.service;

import com.findit.entity.Claim;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    
    public void sendNewClaimNotification(Claim claim) {
        System.out.println("📧 NEW CLAIM #" + claim.getId());
        System.out.println("   Item: " + claim.getItem().getTitle());
        System.out.println("   Claimant: " + claim.getClaimant().getEmail());
        System.out.println("   Answers: " + claim.getAnswers());
    }
    
    public void sendClaimApprovalNotification(Claim claim) {
        System.out.println("📧 CLAIM #" + claim.getId() + " APPROVED");
        System.out.println("   Notify: " + claim.getClaimant().getEmail());
        System.out.println("   Item: " + claim.getItem().getTitle());
    }
    
    public void sendClaimRejectionNotification(Claim claim) {
        System.out.println("📧 CLAIM #" + claim.getId() + " REJECTED");
        System.out.println("   Notify: " + claim.getClaimant().getEmail());
        System.out.println("   Reason: " + claim.getRejectionReason());
    }
}