Jalebi : Java Lean Build
========================

- [ ] First they ignore you
- [ ] Then they laugh at you
- [ ] Then they fight
- [ ] Then you win

Features 
========

* Java based: bootstrapping and configuration are java files.
* Maven file system standard, src/main/jalebi/java for extra config files.
* Subprojects support: top Config file references subprojects's config files.
* Submodule support: add any Jalebi project to another as submodule
* vendoring support: any plugin or library can be vendored.
* Needs JDK21 or later.

User flow
=========

A Jalebi project needs two files at top level, which are standard java sources:
1. Boostrapping file, which downloads jalebi-bootstrap.jar and switchtes to it.
2. Configuration file, which does everything else.

The name of files are not fixed by build system, and can be anything.




Directory Structure
===================
The root of repo should have the copy of `Bootstrap.java`

This should have a link to all the 'modules'
A 'module' maven standard layout, with 



