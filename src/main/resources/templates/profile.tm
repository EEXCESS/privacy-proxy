{
    "username": "<headers.profileUserName>",
    "email": "<headers.profileEmail>",
    "password": "<headers.profilePassword>",
    "privacy": {
        "email": "<headers.ProfilePrivacyEmail>",
        "gender": "<headers.ProfilePrivacyGender>",
        "title": "<headers.ProfilePrivacyTitle>",
        "traces": "<headers.ProfilePrivacyTraces>",
        "geoloc": "<headers.ProfilePrivacyGeoloc>",
        "birthdate": "<headers.ProfilePrivacyBirthdate>",
        "address": "<headers.ProfilePrivacyAddress>"
    },
    "title": "<headers.profileTitle>",
    "lastname": "<headers.profileLastName>",
    "firstname": "<headers.profileFirstName>",
    "gender": "<headers.profileGender>",
    <if(headers.profileBirthDate)>"birthdate": "<headers.profileBirthDate>",<endif>
    "address": {
        "street": "<headers.profileAddressStreet>",
        "postalcode": "<headers.profileAddressPostalCode>",
        "city": "<headers.profileAddressCity>",
        "country": "<headers.profileAddressCountry>",
        "region": "<headers.profileAddressRegion>",
        "district": "<headers.profileAddressDistrict>"
    },
    "topics": <if(headers.profileTopics)>[
	    <first(headers.profileTopics):{v | <v>}>
	    <rest(headers.profileTopics):{v | , <v>}>
    ] <else>{}<endif>
}
