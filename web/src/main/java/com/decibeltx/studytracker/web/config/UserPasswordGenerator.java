package com.decibeltx.studytracker.web.config;

import static org.passay.AllowedCharacterRule.ERROR_CODE;

import org.passay.CharacterData;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
import org.springframework.stereotype.Component;

@Component
public class UserPasswordGenerator {

  private final PasswordGenerator passwordGenerator = new PasswordGenerator();

  private CharacterRule[] getCharacterRules() {
    CharacterRule lowerCaseRule = new CharacterRule(EnglishCharacterData.LowerCase);
    lowerCaseRule.setNumberOfCharacters(2);
    CharacterRule upperCaseRule = new CharacterRule(EnglishCharacterData.UpperCase);
    upperCaseRule.setNumberOfCharacters(1);
    CharacterRule numberRule = new CharacterRule(EnglishCharacterData.Digit);
    numberRule.setNumberOfCharacters(1);
    CharacterData specialChars = new CharacterData() {
      public String getErrorCode() {
        return ERROR_CODE;
      }

      public String getCharacters() {
        return "!@#$%^&*()_+";
      }
    };
    CharacterRule specialCharacterRule = new CharacterRule(specialChars);
    specialCharacterRule.setNumberOfCharacters(1);
    return new CharacterRule[]{lowerCaseRule, upperCaseRule, numberRule, specialCharacterRule};
  }

  public String generatePassword() {
    return passwordGenerator.generatePassword(8, getCharacterRules());
  }

}
