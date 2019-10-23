# dslink-java-v2-history

* Java - version 1.8 and up.
* [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0)


## Overview

This is a library for building DSA historians using [sdk-dslink-java-v2](https://github.com/iot-dsa-v2/sdk-dslink-java-v2).
An overview of DSA can be found [here](http://iot-dsa.org/get-started/how-dsa-works).

This library implements a node hierarchy with functionality that should be
common to most implementations.

## Overview

This is a generic library for building historians.  Implementation Main Nodes must subclass 
HistoryMainNode which is required to provide an instance of HistoryProvider.

## Link Architecture

This section outlines the hierarchy of nodes defined by this link.

- _Main_ - Add database nodes here.
    - _Database_ - Represents a database instance.
        - _History Group Folder_ - History groups can be organized with folders.
            - _History Group_ - A history group defines a collection strategy for 
                    all of it's descendant histories.
                - _History Folder_ - Histories can be organized with folders.
                    - _History Watch_ - A specific path to be trended.
                    - _Numeric History Simulator_ - Simulates a history using on a sine wave.
                    - _Boolean History Simulator_ - Randomly simulates a boolean history.


## Main Node

This is the root node of the link.  It allows you to add database nodes.

_Values_

- Enabled - Can be used to disable history collection for the entire link.
- Status - The health or condition of the link.
- Status Text - Description for the current status.

_Actions_

- New Database - Create a new database node.

## Database Node

Represents a unique database as defined by the specific implementation.

_Values_

- Enabled - Can be used to disable trending and history queries.
- Status - The health or condition of the node.
- Status Text - Description for the current status.
- State - State of the database connection: connecting, connected,
    disconnecting or disconnected.
- Last OK - Timestamp of last successful communication with the database.
- Last Fail - Timestamp of last failed connection to the database.

_Actions_

- Apply Aliases - Put history aliases on all history subscriptions.
    - Overwrite - overwrite an existing alias.
- Edit 
    - Delete - Delete this object and it's subtree.  There are two types of delete:
        - Node Only - Only remove the node from the tree.
        - Node and Data - Remove the node and all backing data.
    - Duplicate - Make a copy of this object and its subtree.
    - Rename - Change the node name.
- New
    - Group Folder - Add a folder for organizing history groups.
    - History Group - Add a new history group.
- Purge - Removes records in the given time range from all histories in the subtree.
    - Time Range - Records with timestamps in this range are removed.

## History Group Node

Represents a set of histories with a common collection strategy.

_Values_

- Enabled - Can be used to disable trending.
- Status - The health or condition of the node.
- Status Text - Description for the current status.
- Interval - Set to collect on a regular interval. Can be combined with COV.
- COV - Set to on to enable change of value collection.  Use min
    interval to throttle and max interval to ensure records get
    written with some regularity.
- Min COV Interval - Regulates the minimum interval between records.
- Max Records - The maximum number of records to maintain in each history.
- Max Age - The oldest record to retain in each history.

_Actions_

- Import History - Given a path to a node with a get history action, will
  create a history child and clone the target history.
- Edit
    - Delete - Delete this object and it's subtree.  There are two types of delete:
        - Node Only - Only remove the node from the tree.
        - Node and Data - Remove the node and all backing data.
    - Duplicate - Make a copy of this object and its subtree.
    - Rename - Change the node name.
- New
    - Folder - Add a folder for organizing histories.
    - History - Add a new history.
- Apply Aliases - Put history aliases on all history subscriptions.
    - Overwrite - overwrite an existing alias to another path.
- Purge - Removes records in the given time range from all histories in the subtree.
    - Time Range - Records with timestamps in this range are removed.

## History Group Folder Node

Use to organize history groups.

_Actions_

- Import History - Given a path to a node with a get history action, will
  create a history child and clone the target history.
- Edit
    - Delete - Delete this object and it's subtree.  There are two types of delete:
        - Node Only - Only remove the node from the tree.
        - Node and Data - Remove the node and all backing data.
    - Duplicate - Make a copy of this object and its subtree.
    - Rename - Change the node name.
- New
    - Group Folder - Add a folder for organizing history groups.
    - History Group - Add a new history group.
- Apply Aliases - Put history aliases on all history subscriptions.
    - Overwrite - overwrite an existing alias to another path.
- Purge - Removes records in the given time range from all histories in the subtree.
    - Time Range - Records with timestamps in this range are removed.

## History Watch Node

Subscribes to a path to create history records.

_Values_

- Enabled - Can be used to disable trending.
- Status - The health or condition of the node.
- Status Text - Description for the current status.
- Watch Path - Path to subscribe to.
- Watch Type - The type of the data source.  Set on the first subscription update.
- Watch Value - Current value of the watch path.
- Watch Status - Current status of the watch path.
- Watch Timestamp - Timestamp of the the watch value and status.
- First Timestamp - The earliest record in the history.
- Last Timestamp- The last record in the history.
- Record Count - The total number of records in the history.
- Timezone - Timezone of the data source.
- Units - Units of the data source.  Only applies to numeric types.
- Precision - The number of decimal places.  Only applies to numeric types.
- Totalized - When true, the historian will automatically delta values in history queries.  
    Only applies to numeric types.

_Actions_

- Edit
    - Delete - Delete this object and it's subtree.  There are two types of delete:
        - Node Only - Only remove the node from the tree.
        - Node and Data - Remove the node and all backing data.
    - Duplicate - Make a copy this object.
    - Rename - Change the node name.
- Get History - Queries the history and returns a table.  The options are:
    - Time Range -
    - Interval -
    - Rollup -
- Apply Alias - Put a history alias on the subscribed point.
    - Overwrite - overwrite an existing alias to another path.
- Add Record - Adds a record to the history.
    - Time Range - Date time range of the query.
    - Interval - The time interval of the results.
    - Rollup - How to combine multiple values in an interval.
    - Real Time - Whether or not to continue to stream values as new records are created.
- Purge - Removes records in the given time range from all histories in the subtree.
    - Time Range - Records with timestamps in this range are removed.
- Overwrite Records - Writes a new value to existing records.
    - Time Range - Records with timestamps in this range will be modified.
    - Value - The new value to write.
    - Status - The new status to write.

## Numeric History Simulator Node

Simulates a double value using a sine wave.  Will build a history using the max age and max number
of records configured on the history group.  Once the history is built, will continue to append
records to it.

_Values_

- Enabled - Can be used to disable trending.
- Status - The health or condition of the node.
- Status Text - Description for the current status.
- Value - The current value of the simulator.
- Wave Period - The length of time it takes to complete one cycle of a sine wave.
- Wave Height - The height of the sine wave from its least to greatest value.
- Wave Offset - The negative or positive value the a amplitude is centered on.
- Update Rate - The time interval to calculate the current value.  How values are stored in the
    the database is determined by the collection strategy of the parent group.
- First Timestamp - The earliest record in the history.
- Last Timestamp- The last record in the history.
- Record Count - The total number of records in the history.
- Timezone - Timezone of the data source.
- Units - Units of the data source.
- Precision - The number of decimal places.

_Actions_

- Edit
    - Delete - Delete this object and it's subtree.  There are two types of delete:
        - Node Only - Only remove the node from the tree.
        - Node and Data - Remove the node and all backing data.
    - Duplicate - Make a copy this object.
    - Rename - Change the node name.
- Get History - Queries the history and returns a table.  The options are:
    - Time Range - Date time range of the query.
    - Interval - The time interval of the results.
    - Rollup - How to combine multiple values in an interval.
    - Real Time - Whether or not to continue to stream values as new records are created.
- Purge - Removes records in the given time range from all histories in the subtree.
    - Time Range - Records with timestamps in this range are removed.
- Overwrite Records - Writes a new value to existing records.
    - Time Range - Records with timestamps in this range will be modified.
    - Value - The new value to write.
    - Status - The new status to write.
    
## Boolean History Simulator Node

Randomly simulates a boolean value.  Will build a history using the max age and max number
of records configured on the history group.  Once the history is built, will continue to append
records to it.

_Values_

- Enabled - Can be used to disable trending.
- Status - The health or condition of the node.
- Status Text - Description for the current status.
- Value - The current value of the simulator.
- True Random - When the current value is true, what percent chance does it have to
    change to false.
- False Random - When the current value is false, what percent chance does it have to
    change to true.
- Update Rate - The time interval to calculate the current value.  How values are stored in the
    the database is determined by the collection strategy of the parent group.
- First Timestamp - The earliest record in the history.
- Last Timestamp- The last record in the history.
- Record Count - The total number of records in the history.
- Timezone - Timezone of the data source.
- True Text - Display text for true values.
- False Text - Display text for false values.

_Actions_

- Edit
    - Delete - Delete this object and it's subtree.  There are two types of delete:
        - Node Only - Only remove the node from the tree.
        - Node and Data - Remove the node and all backing data.
    - Duplicate - Make a copy this object.
    - Rename - Change the node name.
- Get History - Queries the history and returns a table.  The options are:
    - Time Range - Date time range of the query.
    - Interval - The time interval of the results.
    - Rollup - How to combine multiple values in an interval.
    - Real Time - Whether or not to continue to stream values as new records are created.
- Purge - Removes records in the given time range from all histories in the subtree.
    - Time Range - Records with timestamps in this range are removed.
- Overwrite Records - Writes a new value to existing records.
    - Time Range - Records with timestamps in this range will be modified.
    - Value - The new value to write.
    - Status - The new status to write.

## History Folder Node

Use to organize histories.

_Actions_

- Import History - Given a path to a node with a get history action, will
  create a history child and clone the target history.
- Edit
    - Delete - Delete this object and it's subtree.  There are two types of delete:
        - Node Only - Only remove the node from the tree.
        - Node and Data - Remove the node and all backing data.
    - Duplicate - Make a copy of this object and its subtree.
    - Rename - Change the node name.
- New
    - Folder - Add a folder for organizing histories.
    - History - Add a new history.
- Apply Aliases - Put history aliases on subscribed points.
    - Overwrite - overwrite an existing alias to another path.
- Purge - Removes records in the given time range from all histories in the subtree.
    - Time Range - Records with timestamps in this range are removed.


## Acknowledgements

SDK-DSLINK-JAVA

This software contains unmodified binary redistributions of 
[sdk-dslink-java-v2](https://github.com/iot-dsa-v2/sdk-dslink-java-v2), which is licensed 
and available under the Apache License 2.0. An original copy of the license agreement can be found 
at https://github.com/iot-dsa-v2/sdk-dslink-java-v2/blob/master/LICENSE

