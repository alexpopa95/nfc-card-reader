[![Build](https://github.com/alexpopa95/nfc-card-reader/actions/workflows/build.yml/badge.svg?branch=master)](https://github.com/alexpopa95/nfc-card-reader/actions/workflows/build.yml)
[![](https://jitpack.io/v/alexpopa95/nfc-card-reader.svg)](https://jitpack.io/#alexpopa95/nfc-card-reader)

## Gradle

```gradle
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation "com.github.alexpopa95:nfc-card-reader:0.2"
}
```

## How to use

```kotlin
val cardReader = CardReader.newInstance()
val cardReaderObservable = CardReaderObservable.newInstance(cardReader)

cardReaderObservable.event.collect { event: CardReaderEvent ->
    // CardReaderEvent.ReadyToScan
    // CardReaderEvent.StartReading
    // CardReaderEvent.CardLost
    // CardReaderEvent.NFCDisabled
    // CardReaderEvent.NFCNotSupported
    // CardReaderEvent.Error(Throwable)
    // CardReaderEvent.Success(CardData)
}
```

## When NFC is disabled

```kotlin
cardReader.openNfcSettings(Context)
```

## CardData

- `aids` List of card application identifiers
- `types` List of card types
- `state` UNKNOWN, ACTIVE, LOCKED
- `expireDate` Card expiration date as Date object
- `number` Card number (may contain letters)
- `formattedNumber` Card number (only digits)
- `formattedDate` Expiration date (String: MM/yy)
- `expireMonth` Expiration month (String: 08)
- `expireYear` Expiration year (String: 28)
- `isValid` True when card has valid data. You may use partial card data.

### Under the hood

This library is using [com.github.devnied.emvnfccard](https://github.com/devnied/EMV-NFC-Paycard-Enrollment) to read and parse the card data from the nfc chip.