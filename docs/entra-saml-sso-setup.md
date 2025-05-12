# Setting Up Entra ID SAML SSO with Study Tracker

This document provides instructions for setting up an application in Microsoft Entra ID (formerly Azure AD) to enable SAML SSO (Single Sign-On) with Study Tracker.

## Prerequisites

Before you begin, ensure you have:

1. An Entra ID account with administrative privileges
2. Access to your Study Tracker application configuration
3. A Java keystore file for SAML signing (or the ability to create one)

## Setting Up the Application in Entra ID

### Step 1: Create a New Enterprise Application in Entra ID

1. Log in to the [Microsoft Entra admin center](https://entra.microsoft.com)
2. Navigate to **Identity** > **Applications** > **Enterprise applications**
3. Click **+ New application**
4. Select **Create your own application**
5. Enter a name for your application (e.g., "Study Tracker")
6. Select **Integrate any other application you don't find in the gallery (Non-gallery)**
7. Click **Create**

### Step 2: Configure SAML Settings

1. In your new application, go to the **Single sign-on** section
2. Select **SAML** as the sign-on method
3. Configure the following settings:

#### Basic SAML Configuration
1. **Identifier (Entity ID)**: Enter the URI that identifies your Study Tracker application
   - Format: `https://[your-study-tracker-domain]/saml/metadata`
   - Example: `https://studytracker.example.com/saml/metadata`

2. **Reply URL (Assertion Consumer Service URL)**: Enter the URL where Entra ID will send the SAML response
   - Format: `https://[your-study-tracker-domain]/login/saml2/sso/entra`
   - Example: `https://studytracker.example.com/login/saml2/sso/entra`

3. **Sign on URL**: Enter the URL where users will start the login process
   - Format: `https://[your-study-tracker-domain]/login`
   - Example: `https://studytracker.example.com/login`

4. **Relay State**: Leave empty or specify a URL where users should be directed after authentication

#### User Attributes & Claims
1. Configure the following claims:
   - **Unique User Identifier (Name ID)**: Select `user.userprincipalname` or `user.mail` as the source
   - **Name ID Format**: Set to `Email Address`
   - Additional claims (optional):
     - `givenname` -> `user.givenname`
     - `surname` -> `user.surname`
     - `emailaddress` -> `user.mail`
     - `name` -> `user.displayname`

2. Click **Save**

### Step 3: Download SAML Metadata

1. In the **SAML Signing Certificate** section, download the **Federation Metadata XML**
2. Note the **Login URL** (this will be used in the Study Tracker configuration)

### Step 4: Assign Users to the Application

1. Go to the **Users and groups** section of your application
2. Click **+ Add user/group**
3. Select the users or groups who should have access to Study Tracker
4. Click **Assign**

## Configuring Study Tracker for Entra ID SAML SSO

### Step 1: Create a Java Keystore for SAML Signing

If you don't already have a keystore, create one using the following command:

```bash
keytool -genkeypair -alias stsaml -keyalg RSA -keysize 2048 -storetype JKS -keystore saml-keystore.jks -validity 3650
```

You'll be prompted to create a password and provide some information for the certificate.

### Step 2: Configure Study Tracker Properties

Add or update the following properties in your Study Tracker configuration:

```properties
# Enable Entra ID SAML SSO
security.sso=entra-saml

# Entra ID URL (the Login URL from Entra ID)
sso.entra.url=https://login.microsoftonline.com/12345678-1234-1234-1234-123456789012/saml2

# SAML Configuration
saml.audience=https://studytracker.example.com/saml/metadata
saml.idp=https://sts.windows.net/12345678-1234-1234-1234-123456789012/
saml.metadata-url=https://login.microsoftonline.com/12345678-1234-1234-1234-123456789012/federationmetadata/2007-06/federationmetadata.xml
saml.metadata-base-url=https://studytracker.example.com

# Keystore Configuration
saml.keystore.location=classpath:saml-keystore.jks
saml.keystore.alias=stsaml
saml.keystore.password=your_keystore_password

# Optional: Maximum authentication age in seconds (default: 86400 - 24 hours)
saml.max-authentication-age=86400
```

Replace the example values with your actual configuration:

- `sso.entra.url`: The Login URL from Entra ID
- `saml.audience`: The Identifier (Entity ID) you configured in Entra ID
- `saml.idp`: The Issuer URL from Entra ID (typically in the format shown)
- `saml.metadata-url`: The URL to the Federation Metadata XML from Entra ID
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
2. You should be redirected to the Microsoft login page
3. Enter your Microsoft credentials
4. After successful authentication, you should be redirected back to Study Tracker

## Troubleshooting

### Common Issues

1. **SAML Response Validation Failed**
   - Verify that the certificate in your keystore matches the one configured in Entra ID
   - Check that the `saml.audience` property matches the Identifier (Entity ID) in Entra ID

2. **Redirect Loop**
   - Ensure that the `saml.metadata-base-url` is correctly set to your Study Tracker base URL

3. **User Not Found**
   - Verify that the user exists in Study Tracker with the same email address as in Entra ID
   - Check that the user is assigned to the application in Entra ID

4. **Invalid SAML Response**
   - Check the logs for specific error messages
   - Verify that the clock on your Study Tracker server is synchronized

### Debugging

To enable detailed SAML debugging, add the following to your application properties:

```properties
logging.level.org.springframework.security.saml2=DEBUG
```

## Conclusion

You have now successfully set up Entra ID SAML SSO with Study Tracker. Users can now log in to Study Tracker using their Microsoft credentials.

For more information, refer to:
- [Spring Security SAML Documentation](https://docs.spring.io/spring-security/reference/servlet/saml2/index.html)
- [Microsoft Entra ID SAML Documentation](https://learn.microsoft.com/en-us/entra/identity-platform/single-sign-on-saml-protocol)