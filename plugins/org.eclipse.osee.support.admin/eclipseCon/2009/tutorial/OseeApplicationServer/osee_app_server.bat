CMD /K java -Declipse.ignoreApp=true -Xms40m -Xmx512m -Dosgi.compatibility.bootdelegation=true -Dequinox.ds.debug=true -Dorg.osgi.service.http.port=8089 -Dosee.log.default=INFO -Dosee.db.connection.id=postgresqlLocalhost -Dosgi.configuration.area=osee_app_server -jar osee_app_server/platform/org.eclipse.osgi_3.4.2.R34x_v20080826-1230.jar -console