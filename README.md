# Together is Better! Integrating BDI and RL Agents for Safe Learning and Effective Collaboration
Companion code of the ICAART 2025 paper, based on Manuel Parmiggiani's final project for the master degree exam, university of Genoa, academic year 2022/2023.

## AgentSpeak & Jason Examples
If you are not proficient about the use of Jason and AgentSpeak, this directory contains some simple and useful examples to understand how it works, together with the instructions for running them.

## BDI only
Initial version of the project, includes only BDI agents.

This project was developed for the first preliminary testings and conclusions.

## RL sandbox
Training project for the RL agents.

Since RL agents need to be trained before play with BDI agents, this project was developed to make them play in a sandbox environment in order to have them learn a game completion policy.

A teaching agent for safe learning can be used, to make sure that RL agents learn only safe strategies

## BDI + RL
Final version of the project, includes both the BDI agents of the first project and the RL agents of the second project.

Here BDI and pre-trained RL agents can play together.

##
All of the projects were developed using the Jedit IDE, the Jason Java library and the AgentSpeak language.

Extra added libraries: YamlBeans Java library 

Instruction for run the presented projects:
- Download the Jedit IDE and its libraries from https://sourceforge.net/projects/jason/
- Extract the content of the zip file, the executable for the Jedit IDE can be found in the "jedit" directory
- Open the file "doc/tutorials/getting-started/readme.html" and complete the initial setup of the IDE
- Download the YamlBeans jar from https://github.com/EsotericSoftware/yamlbeans/tree/master/lib
- Add the jar file to the "libs" directory
- Open the .mas2j file of the project you want to run from the Jedit IDE
- Launch the project by clicking on the "Run MAS" icon

## Credits
The code has been developed by Manuel Parmiggiani; the ICAART 2025 paper has been written in collaboration with Angelo Ferrando and Viviana Mascardi. You may cite the paper as

@inproceedings{DBLP:conf/icaart/Parmiggiani25,

  author       = {Manuel Parmiggiani and Angelo Ferrando and Viviana Mascardi},

  title        = {Together is Better! {I}ntegrating {BDI} and {RL} Agents for Safe Learning and Effective Collaboration},
  
  booktitle    = {{ICAART}},
  
  pages        = {12},
  
  publisher    = {{SCITEPRESS}},
  
  year         = {2025}

}
