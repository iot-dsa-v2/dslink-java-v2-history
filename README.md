# dslink-java-v2-history

* Java - version 1.8 and up.
* [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0)


## Overview

This is a library for building DSA historians using [sdk-dslink-java-v2](https://github.com/iot-dsa-v2/sdk-dslink-java-v2).
An overview of DSA can be found [here](http://iot-dsa.org/get-started/how-dsa-works).

This library implements a node hierarchy with functionality that should be
common to most implementations.

## Creating Implmentations

TODO

## Link Architecture

This section outlines the hierarchy of nodes defined by this link.

- _Main_ - Create databases here.
    - _Database_ - Represents a unique database instance.
        - _History Group Folder_ - History groups can be organized with folders.
            - _History Group_ - A history group defines how trending is
              to be performed for all of it's descendant histories.
                - _History Folder_ - Histories can be organized with folders.
                    - _History_ - A specific path to be trended.


## Main Node

This is the root node of the link.  It allows you to add databases.

_Values_

- Enabled - Can be used to disable history collection in the entire link.
- Status - The health or condition of the link.
- Status Text - Description for the current status.

_Actions_

- New Database - Create a new database node.

## Database Node

Represents a unique database as defined by the specific implementation.

_Values_

- Enabled - Can be used to disable trending and history queries.
- Status - The health or condition of the link.
- Status Text - Description for the current status.
- State - State of the database connection.
- Last OK - Timestamp of last successful communication with the database.
- Last Fail - Timestamp of last failed connection to the database.

_Actions_

 - Edit Actions
    - Delete - Delete the history group.
        - Node Only - Only remove the node from the tree.
        - Node and Data - Remove the node and all backing data.
    - Duplicate - Make a copy of the the database and its subtree.
    - Rename - Change the node name.
 - New Actions
    - Group Folder - Add a folder for organizing history groups.
    - History Group - Add a new history group.
 - Purge - Removes records in the given time range from all histories.
    - Time Range - Records with timestamps in this range are removed.

## History Group Node

Represents a set of histories with a common collection strategy.

_Values_

- Enabled - Can be used to disable trending.
- Status - The health or condition of the link.
- Status Text - Description for the current status.
- Interval - Set to collect on a regular interval.  0 disables and
    this can be combined with COV.
- COV - Set to true to enable change of value collection.  Use min
    interval to throttle and max interval to ensure records get
    written with some regularity.
- Min COV Interval - Regulates the minimum interval between records.
- Max COV Interval - Ensures a record is written this amount of time after the
    last record.

_Actions_

- Import History - Given a path to a node with a get history action, will
  create a history child and clone the target history.
- Edit
    - Delete - Delete the history group.  There are two types of delete:
        - Node Only - Only remove the node from the tree.
        - Node and Data - Remove the node and all backing data.
    - Duplicate - Make a copy of the history group and its subtree.
    - Rename - Change the node name.
- New
    - Folder - Add a folder for organizing histories.
    - History - Add a new history.
- Apply Aliases - Put history aliases on subscribed points.
    - Overwrite - overwrite an existing alias to another path.
- Purge - Removes records from the history.
    - Time Range - Records with timestamps in this range are removed.

## History Node

Represents a single history table.

_Values_

- Enabled - Can be used to disable trending.
- Status - The health or condition of the link.
- Status Text - Description for the current status.
- Watch Path - Path to subscribe to.
- Data Type - The type of the data source.  Set when the path is first
    set, but can be changed to override native format of the data source.
- First Record - The earliest record in the history.
- Last Record - The latest record in the history.
- Record Count - The total number of records in the history.
- Timezone - Timezone of the data source.
- Units - Units of the data source.
- Totalized - Only applies to numeric types.  When true, the historian will
    automatically delta values in history queries.

_Actions_

- Edit
    - Delete - Delete the history group.  There are two types of delete:
        - Node Only - Only remove the node from the tree.
        - Node and Data - Remove the node and all backing data.
    - Duplicate - Make a copy of the history.
    - Rename - Change the node name.
- Get History - Queries the history and returns a table.  The options are:
    - Time Range -
    - Interval -
    - Rollup -
- Apply Alias - Put a history alias on the subscribed point.
    - Overwrite - overwrite an existing alias to another path.
- Add Record - Adds a record to the history.
    - Timestamp - If left empty will use the current time.
    - Value - Will match the data type.
    - Status - The new status to write.
- Purge - Removes records from the history.
    - Time Range - Records with timestamps in this range are removed.
- Overwrite Records - Writes a new value to existing records.
    - Time Range - Records with timestamps in this range will be modified.
    - Value - The new value to write.
    - Status - The new status to write.



## Acknowledgements

SDK-DSLINK-JAVA

This software contains unmodified binary redistributions of 
[sdk-dslink-java-v2](https://github.com/iot-dsa-v2/sdk-dslink-java-v2), which is licensed 
and available under the Apache License 2.0. An original copy of the license agreement can be found 
at https://github.com/iot-dsa-v2/sdk-dslink-java-v2/blob/master/LICENSE

