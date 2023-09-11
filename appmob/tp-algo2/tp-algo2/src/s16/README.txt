S16 - Ex. 1 - N-Queens Problem
==============================

To visualize the behaviour of your algorithm, some setup is needed. 

1. The queens_fx_s16.jar library has to be imported (it uses JavaFX).
   With IntelliJ: File -> Project Structure -> Modules 
                       -> Dependencies -> + -> JARs or directory
                       -> queens_fx_s16.jar
   This component supposes that your Queens class is in the package named s16.
   
2. JavaFX has to be setup as a dependency of the project. There are different 
   ways to do that, maybe I'd suggest (tell me if there is a better option):
   
          File -> Project Structure 
               -> Libraries -> + (New Project Library) 
               -> From Maven… -> org.openjfx:javafx-controls:17
               -> (Transitive dependencies) -> Ok -> Ok
               
Good luck!