FROM quay.io/wildfly/wildfly:latest-jdk21

ADD fanny.war /opt/jboss/wildfly/standalone/deployments/
#test war
ADD node-info.war /opt/jboss/wildfly/standalone/deployments/

USER root
RUN chown jboss:jboss /opt/jboss/wildfly/standalone/deployments/*

USER jboss