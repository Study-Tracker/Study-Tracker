# Setting Up Okta SAML SSO with Study Tracker

This document provides instructions for setting up an application in Okta to enable SAML SSO (Single Sign-On) with Study Tracker.

## Prerequisites

Before you begin, ensure you have:

1. An Okta account with administrative privileges
2. Access to your Study Tracker application configuration
3. A Java keystore file for SAML signing (or the ability to create one)

## Setting Up the Application in Okta

### Step 1: Create a New SAML Application in Okta

1. Log in to your Okta Admin Dashboard
2. Navigate to **Applications** > **Applications**
3. Click **Create App Integration**
4. Select **SAML 2.0** as the Sign-in method and click **Next**

### Step 2: Configure SAML Settings

#### General Settings
1. Enter a name for your application (e.g., "Study Tracker")
2. Optionally, upload a logo for the application
3. Click **Next**

#### Configure SAML Settings
1. **Single Sign-On URL**: Enter the URL where Okta will send the SAML response
   - Format: `https://[your-study-tracker-domain]/login/saml2/sso/okta`
   - Example: `https://studytracker.example.com/login/saml2/sso/okta`

2. **Audience URI (SP Entity ID)**: Enter the URI that identifies your Study Tracker application
   - Format: `https://[your-study-tracker-domain]/saml/metadata`
   - Example: `https://studytracker.example.com/saml/metadata`

3. **Default RelayState**: Leave empty or specify a URL where users should be directed after authentication

4. **Name ID Format**: Set to `EmailAddress`

5. **Application Username**: Select `Email`

6. **Attribute Statements (optional)**: Configure additional user attributes to be sent to Study Tracker
   - Common attributes:
     - `firstName` -> `user.firstName`
     - `lastName` -> `user.lastName`
     - `email` -> `user.email`
     - `displayName` -> `user.displayName`

7. Click **Next**

### Step 3: Feedback and Finish

1. Select whether you're a customer or partner
2. Click **Finish**

### Step 4: Assign Users to the Application

1. In your new application, go to the **Assignments** tab
2. Click **Assign** and select **Assign to People** or **Assign to Groups**
3. Select the users or groups who should have access to Study Tracker
4. Click **Assign** and then **Done**

### Step 5: Collect SAML Configuration Information

After creating the application, collect the following information from Okta:

1. **Identity Provider Issuer (IdP Entity ID)**
   - Found in the **Sign On** tab of your application
   - Example: `http://www.okta.com/exk7iiv6in00VLoNW4x7`

2. **Identity Provider Single Sign-On URL**
   - Found in the **Sign On** tab of your application
   - Example: `https://example.okta.com/app/example_studytracker_1/exk7iiv6in00VLoNW4x7/sso/saml`

3. **Identity Provider Metadata URL**
   - Found in the **Sign On** tab of your application
   - Example: `https://example.okta.com/app/exk7iiv6in00VLoNW4x7/sso/saml/metadata`

4. **X.509 Certificate**
   - Download the certificate from the **Sign On** tab of your application
   - You'll need this to verify SAML responses

## Configuring Study Tracker for Okta SAML SSO

### Step 1: Create a Java Keystore for SAML Signing

If you don't already have a keystore, create one using the following command:

```bash
keytool -genkeypair -alias stsaml -keyalg RSA -keysize 2048 -storetype JKS -keystore saml-keystore.jks -validity 3650
```

You'll be prompted to create a password and provide some information for the certificate.

### Step 2: Configure Study Tracker Properties

Add or update the following properties in your Study Tracker configuration:

```properties
# Enable Okta SAML SSO
security.sso=okta-saml

# Okta URL (the Single Sign-On URL from Okta)
sso.okta.url=https://example.okta.com/app/example_studytracker_1/exk7iiv6in00VLoNW4x7/sso/saml

# SAML Configuration
saml.audience=https://studytracker.example.com/saml/metadata
saml.idp=http://www.okta.com/exk7iiv6in00VLoNW4x7
saml.metadata-url=https://example.okta.com/app/exk7iiv6in00VLoNW4x7/sso/saml/metadata
saml.metadata-base-url=https://studytracker.example.com

# Keystore Configuration
saml.keystore.location=classpath:saml-keystore.jks
saml.keystore.alias=stsaml
saml.keystore.password=your_keystore_password

# Optional: Maximum authentication age in seconds (default: 86400 - 24 hours)
saml.max-authentication-age=86400
```

Replace the example values with your actual configuration:

- `sso.okta.url`: The Single Sign-On URL from Okta
- `saml.audience`: The Audience URI (SP Entity ID) you configured in Okta
- `saml.idp`: The Identity Provider Issuer (IdP Entity ID) from Okta
- `saml.metadata-url`: The Identity Provider Metadata URL from Okta
- `saml.metadata-base-url`: The base URL of your Study Tracker application
- `saml.keystore.location`: The location of your Java keystore file
- `saml.keystore.alias`: The alias for the key in your keystore
- `saml.keystore.password`: The password for your keystore

### Step 3: Add the Keystore File to Your Application

Place the `saml-keystore.jks` file in your application's classpath, typically in the `src/main/resources` directory.

### Step 4: Restart Your Application

Restart your Study Tracker application to apply the changes.

## Testing the Integration

1. Access your Study Tracker application
2. You should be redirected to the Okta login page
3. Enter your Okta credentials
4. After successful authentication, you should be redirected back to Study Tracker

## Troubleshooting

### Common Issues

1. **SAML Response Validation Failed**
   - Verify that the certificate in your keystore matches the one configured in Okta
   - Check that the `saml.audience` property matches the Audience URI in Okta

2. **Redirect Loop**
   - Ensure that the `saml.metadata-base-url` is correctly set to your Study Tracker base URL

3. **User Not Found**
   - Verify that the user exists in Study Tracker with the same email address as in Okta
   - Check that the user is assigned to the application in Okta

4. **Invalid SAML Response**
   - Check the logs for specific error messages
   - Verify that the clock on your Study Tracker server is synchronized

### Debugging

To enable detailed SAML debugging, add the following to your application properties:

```properties
logging.level.org.springframework.security.saml2=DEBUG
```

## Conclusion

You have now successfully set up Okta SAML SSO with Study Tracker. Users can now log in to Study Tracker using their Okta credentials.

For more information, refer to:
- [Spring Security SAML Documentation](https://docs.spring.io/spring-security/reference/servlet/saml2/index.html)
- [Okta SAML Documentation](https://developer.okta.com/docs/concepts/saml/)