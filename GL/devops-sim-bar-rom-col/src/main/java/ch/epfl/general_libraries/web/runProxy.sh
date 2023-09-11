#!/bin/sh
java -Djava.net.preferIPv4Stack=true ch.epfl.general_libraries.web.TransparentProxy proxy -local 443 -redirIP tcomsrv2.epfl.ch -redirport 80 -killport 2001