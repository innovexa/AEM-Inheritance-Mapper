# AEM Component Architecture Graph

## Why?

Building large AEM projects involves developing components between many developers.
Often its a good idea to overlay other components. This extending action is hard to keep track of.
This project allows you to create a diagram of **inheritance** and **composition** of components.

## Requirements

* Java 1.7+

* [GraphViz](http://www.graphviz.org/)

  Must be runnable from the command line (`dot -V`)

## Usage

1. Download the `jar` locally from the [releases section](https://github.com/innovexa/AEMComponentArchitectureGraph/releases)

2. Run the `jar` with the first argument being the component directory of your project

  Example:

  `java -jar AEMComponentArchitectureGraph-0.1.jar ui.apps/src/main/jcr_root/apps/company/components`

## License

see [LICENSE](https://raw.githubusercontent.com/innovexa/AEMComponentArchitectureGraph/master/LICENSE)


