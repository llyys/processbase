mvn install:install-file -Dfile=esteid-vaadin-component-0.1.1.jar -DgroupId=ee.smartlink.esteid -DartifactId=esteid-vaadin-component -Dversion=0.1.1 -Dpackaging=jar
mvn install:install-file -Dfile=xtee-connector-1.0.jar -DgroupId=ee.smartlink.xtee -DartifactId=xtee-connector -Dversion=1.0 -Dpackaging=jar
mvn install:install-file -Dfile=forms-model-5.5.1.jar -DgroupId=org.ow2.bonita -DartifactId=forms-model -Dversion=5.5.1 -Dpackaging=jar
mvn install:install-file -Dfile=forms-server-5.5.1.jar -DgroupId=org.ow2.bonita -DartifactId=forms-server -Dversion=5.5.1 -Dpackaging=jar
mvn install:install-file -Dfile=xtee-client-adit-1.0.0.jar -DgroupId=ee.finestmedia.xtee -DartifactId=xtee-client-adit -Dversion=1.0.0 -Dpackaging=jar

mvn install:install-file -Dfile=chartengineapi.jar -DartifactId=chart.engine.api -DgroupId=org.eclipse.birt -Dversion=2.6.1 -Dpackaging=jar
mvn install:install-file -Dfile=coreapi.jar -DartifactId=core.api -DgroupId=org.eclipse.birt -Dversion=2.6.1 -Dpackaging=jar
mvn install:install-file -Dfile=dataadapterapi.jar -DartifactId=data.adapter.api -DgroupId=org.eclipse.birt -Dversion=2.6.1 -Dpackaging=jar
mvn install:install-file -Dfile=dteapi.jar -DartifactId=data.engine.api -DgroupId=org.eclipse.birt -Dversion=2.6.1 -Dpackaging=jar
mvn install:install-file -Dfile=engineapi.jar -DartifactId=report.engine.api -DgroupId=org.eclipse.birt -Dversion=2.6.1 -Dpackaging=jar
mvn install:install-file -Dfile=modelapi.jar -DartifactId=report.model.api -DgroupId=org.eclipse.birt -Dversion=2.6.1 -Dpackaging=jar
mvn install:install-file -Dfile=scriptapi.jar -DartifactId=script.api -DgroupId=org.eclipse.birt -Dversion=2.6.1 -Dpackaging=jar

mvn install:install-file -Dfile=jdigidoc\jdigidoc-3.7.2.652.jar -DartifactId=jdigidoc -DgroupId=ee.eesti.id -Dversion=3.7.2.652 -Dpackaging=jar
mvn install:install-file -Dfile=jdigidoc\jdigidoc-3.7.2.652-source.jar -DartifactId=jdigidoc -DgroupId=ee.eesti.id -Dversion=3.7.2.652 -Dpackaging=jar  -Dclassifier=sources

