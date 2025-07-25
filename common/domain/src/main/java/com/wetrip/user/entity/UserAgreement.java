package com.wetrip.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAgreement {

  @Id
  @Column(nullable = false)
  private Long id;

  @Column(nullable = false)
  private Boolean genderConsentProvided = false;

  @Column(nullable = false)
  private Boolean birthdayConsentProvided = false;

  @Column(nullable = false)
  private Boolean contactConsentProvided = false;

  @Column(nullable = false)
  private Boolean ageConsentProvided = false;

  @Column(nullable = false)
  private Boolean emailConsentProvided = false;

  @OneToOne(fetch = FetchType.LAZY)
  @MapsId
  @JoinColumn(name = "id")
  private User user;

  public static UserAgreement from(
      User user,
      boolean ageConsentProvided,
      boolean birthdayConsentProvided,
      boolean contactConsentProvided,
      boolean emailConsentProvided,
      boolean genderConsentProvided
  ) {
    return UserAgreement.builder()
        .user(user)
        .ageConsentProvided(ageConsentProvided)
        .birthdayConsentProvided(birthdayConsentProvided)
        .contactConsentProvided(contactConsentProvided)
        .emailConsentProvided(emailConsentProvided)
        .genderConsentProvided(genderConsentProvided)
        .build();
  }
}