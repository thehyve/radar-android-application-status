# Application status plugin for RADAR-pRMT

Plugin that sends application statuses about the RADAR pRMT app.

## Installation

First, add the plugin code to your application:

```gradle
repositories {
    maven { url  'http://dl.bintray.com/radar-cns/org.radarcns' }
}

dependencies {
    compile 'org.radarcns:radar-android-application-status:0.1-alpha.1'
}
```

Set the `ntp_server` property in the configuration settings to synchronize with an external NTP server.

## Contributing

Code should be formatted using the [Google Java Code Style Guide](https://google.github.io/styleguide/javaguide.html), except using 4 spaces as indentation. Make a pull request once the code is working.
