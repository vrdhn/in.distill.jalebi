


default: fmt run

fmt:
	@java -jar ../google-java-format-1.17.0-all-deps.jar  --aosp --replace  $$(find . -name '*.java')


run:
	@java JalebiBuildBootstrapped.java 

