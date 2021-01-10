# Command Line Interface

The Study Tracker command line interface (CLI) allows you to import and export data directly from the database, without the need to interface with the web application or web services. It can be run by building the `study-tracker-cli` module JAR and executing it as a command line application.

## Usage

To view CLI usage, run the application with the `-h` or `--help` flags:

```bash
java -jar study-tracker-cli.jar -h
```

## Import

You can import data into the Study Tracker database using the `import` command. This can be used to seed the application database with initial state data, or to batch-load data from another source. Data should be contained in files that conform to the format specified below.

To run the data import, use the following syntax:

```bash
java -jar study-tracker-cli.jar import file1.yml file2.yml
```

To wipe the database clean before import, use the `-D` or `--drop-database` command:

```bash
java -jar study-tracker-cli.jar import file1.yml file2.yml --drop-database
```

