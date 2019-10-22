FROM tomcat:9.0.12

MAINTAINER admin <qige.chen@sv.cmu.edu>

ADD build/libs/app-api.war /usr/local/tomcat/webapps/

CMD ["catalina.sh", "run"]

EXPOSE 8080