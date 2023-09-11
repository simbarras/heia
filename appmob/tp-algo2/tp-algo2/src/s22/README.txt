S22 - Geometry algorithms
=========================

The archive cgeom_s22.jar contains a toy application that helps to visually 
verify the behaviour of your s22.Geom.* methods. 

Import that library and add it as a dependency of your project. 
With IntelliJ: File -> Project Structure -> Modules 
                     -> Dependencies -> + -> JARs or directory
                     -> cgeom_s22.jar

Then you can run s22.Geom.main() (which calls s22.CG.main()).

This component supposes that your source code (Geom.java, 
PointComparator.java and Segm.java) is in the s22 package.
