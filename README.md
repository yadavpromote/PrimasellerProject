# PrimasellerProject

Steps to run project:
1. Clone project in local box 
2. change directory to project folder 
3. run 'ant jar' command to create jar file or this project is netbeans project so project jar file can be created from netbeans as well after importing to netbeans, If using eclipse then import project in eclipse as netbeans project.
4. run jar file with arguments 'java -jar [jar_file_path] --books=/path/to/books.list --sales=/path/to/sales.list --top_selling_books=3 --top_customers=2 --sales_on_date=2018-02-01'

Example:  java -jar .\dist\PrimasellerProject.jar --books=.\\books.csv --sales=.\\sales.csv --top_selling_books=3 --top_customers=2 --sales_on_date=2018-08-01

Note:- Change parameters accordingly. 
