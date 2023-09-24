package com.bartek.supportportal.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.Date;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "users")
public class User implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(nullable = false, updatable = false)
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private Long id;

  private String userId;

  @NotBlank(message = "First name is mandatory")
  private String firstName;

  @NotBlank(message = "Last name is mandatory")
  private String lastName;

  @NotBlank(message = "Username name is mandatory")
  private String username;

  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private String password;

  @NotBlank(message = "Email is mandatory")
  @Email(message = "Email format is not valid")
  private String email;

  private String profileImageUrl;
  private Date lastLoginDate;
  private Date lastLoginDateDisplay;
  private Date joinDate;
  private String role;
  private boolean isActive;
  private boolean isNonLocked;
}
