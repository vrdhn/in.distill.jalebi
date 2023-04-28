Jalebi.jar
===========

A Jalebi project has a 'Jalebi.java' file at the top level,
and at the root of any subprojects. The Jalebi.java is not different
between various levels, and it's possible to include an completely independent
projects as a module, without changing it's Jalebi.java

The 'Jalebi.java' should not have any  dependency beyond standard jdk.
It has code to download 'jalebi-bootstrap.jar' from a well known location
and check it's checksum/signature.

Jelabi.jar would load bootstrap.jar, jump to the main in in.distill.jalebi.bootstrap.Main.


jalebi-bootstrap.jar
====================
jalebi-bootstrap.jar exists to make Jalebi.jar as small as possible.



How does Jalebi bootstrap --or-- how to vendor Jalebi
=====================================================

Jalebi itself is an example of a vendored build.

Non-Vendored build: jalebi-bootstrap.jar will be downloaded from maven.

Vendored build:  jalebi-bootstrap should be part of source code. It'll
  be compiled using JDK compiler interface. 

While Jalebi itself may be vendored or note, it allows any module to 
be vendored. 






