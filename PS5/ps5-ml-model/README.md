# Fusion prediction

## Process data

```bash
python data_processor.py -h
usage: data_processor.py [-h] [-t] [-r] [-d] [-a]

Create the new dataset.

optional arguments:
  -h, --help         show this help message and exit
  -t, --translate    Translate the name to smiles
  -r, --rewrite      Rewrite training set and test set
  -d, --descriptors  Compute the descriptors
  -a, --analyse      Analyse the dataset
```

Every step is optional and if it's not specified, the script will load the data from the file.

## Train the model

```bash
python fusion_predition.py -h
usage: fusion_predictor.py [-h] [--version] [-o] [-n {minmax,standard}] [-b]
                           [-a {nusvr,svr,randomforest,gradientboosting}]
                           [-p {pca,kbest}] [-k KFEATURES] [-s {random,grid}]
                           {analyse,train}

Find the fusion point of an ionic liquid.

positional arguments:
  {analyse,train}       Action to perform

optional arguments:
  -h, --help            show this help message and exit
  --version             show program`s version number and exit
  -o, --old             Use the old data
  -n {minmax,standard}, --normalise {minmax,standard}
                        Normalise the data
  -b, --both            Normalise feature and target
  -a {nusvr,svr,randomforest,gradientboosting}, --algorithm {nusvr,svr,randomforest,gradientboosting}
                        Algorithm to use
  -p {pca,kbest}, --preprocessing {pca,kbest}
                        Preprocessing to use
  -k KFEATURES, --kfeatures KFEATURES
                        Number of features to keep
  -s {random,grid}, --search {random,grid}
                        Search algorithm to use

Process finished with exit code 0
```

This script is easy to use and we can do a lot of experiments with it.
For the grid and random search, we need to specify the param_grid in the script.
