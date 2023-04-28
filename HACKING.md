Modules
=======

# Bootstrap.java
Gets `jalebi-bootstrap` and yields control to it.

Can be replaced by script downloading `jalebi-bootstrap` and executing it.

Invoked by user as `java Bootstrap.java <Arguments...>`

Most common flow is to get this file from root of `jalebi` repo 
and commit it at the root of project. There are a couple of configurable
parameters in it.

Features:
 * compiled vendored `jalebi-bootstrap` if defined.
 * downloads the jalebi-bootstrap.jar
 * checks signature of download jars
 * launches the `jalebi-bootstrap.jar`

Limitations:
 * Can't have any dep beyond jdk
 * Can't have nested class.
 * Has to be single self contained file.

Variations:
 * The non-vendored version which reduces some code.

# jalebi-bootstrap
Features:
 * compile Jalebi config files
 * discover dependencies
 * compile vendored plugins
 * load plugins
 * https download of pom and xml
 * 


	
