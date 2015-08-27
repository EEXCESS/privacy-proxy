function extractUrl() {
	ServiceURL=$1
	if [ $1 = "local-eclipse" ]; then # local through Eclipse
		ServiceURL="http://localhost:8080/eexcess-privacy-proxy/" 
	else 
		if [ $1 = "local" ]; then # local manual deployment
			ServiceURL="http://localhost:8080/eexcess-privacy-proxy-1.0-SNAPSHOT/"
		else 
			if [ $1 = "remote-dev" ]; then # development server
				ServiceURL="http://eexcess-dev.joanneum.at/eexcess-privacy-proxy-1.0-SNAPSHOT/"
			else 
				if [ $1 = "remote-dev-test" ]; then # development server
					ServiceURL="http://eexcess-dev.joanneum.at/eexcess-privacy-proxy-1.0-DEV-SNAPSHOT/"
				fi
			fi
		fi
	fi
	echo $ServiceURL;
}

function printVerdictJson() {
	if [[ $2 == "<html>"* ]]; then
		echo ">>" $1 "failed"
	else
		echo "  " $1 "passed"
	fi
}

function printVerdictFile() {
	if [ -f $2 ]; then
		echo "  " $1 "passed"
	else 
		echo ">>" $1 "failed"
	fi
}