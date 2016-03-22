# ReCitable #

Data citation made reproducible.

# Getting started! #

As dataset change over time queries on these datasets are often invalided or not reproducible because of the changes in
the dataset. A possible solution is to version the datasets as well as the queries and save the version of the dataset
the query was run on.

This is a simple prototype that saves and versions the datasets and queries in [Git](https://git-scm.com/), a
distributed version control system. Furthermore it allows to:

* Test queries on existing datasets and save them with a corresponding _persistent identifier_.
* Rerun queries that can be selected from their _persistent identifier_.

# How to test? #

## Data collection ##

_Prerequisite: Git must be installed on your computer._

_At the moment datasets can only be registered via Git directly!_
_At the moment datasets can only be in CSV with ; as field separator!_
_You can use e.g. sed to replace the field separators._

Prepare a git repository that automatically collects your data. E.g. create a git repository by running:

    cd /home/pi/MetroData/Database
    git init

Create a script that crawls and commits your data:

    #!/bin/bash
    cd /home/pi/MetroData/
    # Get the data from an open data portal
    # (in this case meteorological data  from Austria)
    curl --retry 5 -L -o tawes1h.csv http://www.zamg.ac.at/ogd/
    # Delete the header so it doesn't corrupt your data
    sed -i -e '1d' tawes1h.csv
    # Pipe the data into a dataset in the database folder
    cat tawes1h.csv >> Database/ZAMG-MetroData.csv
    rm tawes1h.csv
    cd Database/
    # Commit the change to the database
    git checkout master
    message=`date +%Y-%m-%d.%H:%M`
    git commit -am "ZAMG-MetroData $message"

Create a cronjob that runs your script automatically.

## Starting the application ##

_Prerequisite: [Maven](https://maven.apache.org/) must be installed on your computer._

Download the _ReCitable_ repository from [Github](http://github.com/).


Change to the `DOWLOAD_DIR` and run the following command:

    mvn clean install

Then change to _resources_ directory of the web application by running:

    cd DOWLOAD_DIR/webapp/src/main/resources

_At the moment only a single repository for datasets is supported!_

Change the parameter `databaseLocation` to point to the repository you created before e.g.
`/home/pi/MetroData/Database`.

Change to the web application directory and run the Jetty server with the following command:

    cd DOWLOAD_DIR/webapp
    mvn jetty:run

You can now access the web application at the URL [http://localhost:8080](http://localhost:8080).

There you can:

* Select a dataset and try different queries on it. Standard SQL can be used in the text area, but you always need to
provide the dataset name as table reference e.g. `SELECT * FROM ZAMG-MetroData`.
* Assign a PID and a description to the query and save it.
* Rerun a query that was saved before exactly the way it was run before.
* Returns to the start any time you want by using the logo link.

_Be aware that this is a prototype and you can easily destroy your database by altering the datasets!_
