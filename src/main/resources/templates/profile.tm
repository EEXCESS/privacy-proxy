{
    "username": "<headers.profileUserName>",
    "email": "<headers.profileEmail>",
    "password": "<headers.profilePassword>",
    "privacy": {
        "email": "<headers.profilePrivacyEmail>",
        "gender": "<headers.profilePrivacyGender>",
        "title": "<headers.profilePrivacyTitle>",
        "traces": "<headers.profilePrivacyTraces>",
        "geoloc": "<headers.profilePrivacyGeoloc>",
        "age": "<headers.profilePrivacyAge>",
        "address": "<headers.profilePrivacyAddress>"
    },
    "title": "<headers.profileTitle>",
    "lastname": "<headers.profileLastName>",
    "firstname": "<headers.profileFirstName>",
    "gender": "<headers.profileGender>",
    <if(headers.profileBirthDate)>"birthdate": "<headers.profileBirthDate>,"<endif>
    "address": {
        "street": "<headers.profileAddressStreet>",
        "postalcode": "<headers.profileAddressPostalCode>",
        "city": "<headers.profileAddressCity>",
        "country": "<headers.profileAddressCountry>"
    },
    "topics": <if(headers.profileTopics)>[
	    <first(headers.profileTopics):{v | "<v>"}>
	    <rest(headers.profileTopics):{v | , "<v>"}>
    ] <else>{}<endif>
}