{
	"query":{
		"bool": {
			"should": [
				{
					"term":{
						"_id": "<exchange.properties.user_id>"
					}
				},
				{
					"term":{
						"email": "<exchange.properties.user_email>"
					}
				},
				{
					"term":{
						"username": "<exchange.properties.user_username>"
					}
				}
			]
		}
	},
	"from":0,
	"size":50,
	"sort":[]
}
