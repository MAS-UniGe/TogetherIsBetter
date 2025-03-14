# Together is Better! Integrating BDI and RL Agents for Safe Learning and Effective Collaboration
Companion code of the ICAART 2025 paper, based on Manuel Parmiggiani's final project for the master degree exam, University of Genoa, academic year 2022/2023.   
The slides of the ICAART presentation are available at https://github.com/MAS-UniGe/TogetherIsBetter/blob/main/ICAART2025-presentation-Parmiggiani-Mascardi-Ferrando.pdf?raw=true, while the video is available at https://www.dropbox.com/scl/fi/ojg6srq7ihqzjvy5t9u5m/Together-is-better-ICAART-2025-video-20250212_111948-Meeting-Recording.mp4?rlkey=zpjbma71h9vslr9504bp0e5s6&dl=0

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

Parmiggiani, M.; Ferrando, A. and Mascardi, V. (2025). Together Is Better! Integrating BDI and RL Agents for Safe Learning and Effective Collaboration.  In Proceedings of the 17th International Conference on Agents and Artificial Intelligence - Volume 3, ISBN 978-989-758-737-5, ISSN 2184-433X, pages 48-59. 

@inproceedings{DBLP:conf/icaart/Parmiggiani25,  
  author       = {Manuel Parmiggiani and Angelo Ferrando and Viviana Mascardi},  
  title        = {Together is Better! {I}ntegrating {BDI} and {RL} Agents for Safe Learning and Effective Collaboration},  
  booktitle    = {Proceedings of the 17th International Conference on Agents and Artificial Intelligence - Volume 3},   
  pages        = {48--59},    
  publisher    = {{SCITEPRESS}},  
  year         = {2025}  
}

The paper will be made available on this web site, as soon as SCITEPRESS will grant us the permission
