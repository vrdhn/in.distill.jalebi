
# Folder Structure

* Group-ID should be directory of the project e.g.: in.distill.jalebi
* Artifact-ID is the module-name e.g.: jalebi.core




# Layout

```
com.example.[project] { top level, maven's group id }
+ - [Project]Build.java { copy from jalebi, add modules to list }
+ - project.module1 { java's module system, with maven std file sys inside }
    + - src/jalebi/java/ { optional, can build jar and publish to maven without it }
		+ - module-info.java { mandatory }
		+ - com/example/project/jalebi/**/*.java
	+ - src/main/java { claimed by 'Java' }
		+ - module-info.java { mandatory }
		+ - com/example/project/**/*.java
	+ - src/main/resources { claimed by 'Java' }
	+ - src/test/java { claimed by 'JavaTest' }
		+ - module-info.java { mandatory }
		+ - com/example/project/**/*.java
	+ - src/test/resources { claimed by 'Java' }
   
```

# Glossary

- projectRoot --> repo root, should have .git and all the modules, and build script.
- moduleRoot --> Each directory named 'a.b.c' in the projectRoot
- 
