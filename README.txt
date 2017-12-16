DailyInputs folder holds 5 folders, one for each day. Each day folder has 3 input sessions in it.
TransactionSummaries folder holds 5 .txt files, one for each day 

**************************************************************
*****master accounts file needs to be wiped when starting*****
**************************************************************

frontEnd takes three arguments:
1. valid accounts list
2. transaction summary file (appends to this, so no need to route output elsewhere, BUT the same one cannot be used for each day)
3. inputs.txt file

backEnd takes four arguments:
1. old master accounts file
2. transaction summary file 
3. new master accounts file
4. new valid accounts file

a new transaction summary file will be needed for each day, because frontEnd appends to the file instead of overwriting it.
a new input file is needed for each session of each day. 
only one valid accounts list is needed, because backEnd overwrites it instead of appending to it
only one master accounts file is needed because backEnd overwrites it instead of appending to it
	- however we will use different master accounts files, an old and a new, just to be save and to
	make it easier to follow the control flow

*******************************************************************************************************
*****There is no need to route output to other text files for either the front end or the back end*****
*******************************************************************************************************