sop4j-dbutils
=============

Overview
--------

[commons-dbutils](http://commons.apache.org/proper/commons-dbutils/) is a JDBC utility library that has been around [since 2003](http://commons.apache.org/proper/commons-dbutils/changes-report.html). A number of projects use it to ensure that JDBC connections are properly closed. The main goals of the project are:
*  **Small**: You should be able to understand the whole package in a short amount of time.
* **Transparent**: DbUtils doesn't do any magic behind the scenes. You give it a query, it executes it and cleans up for you.
* **Fast**: You don't need to create a million temporary objects to work with DbUtils.

Why sop4j-dbutils?
------------------

A natural question is to ask is, "Why fork commons-dbutils, why not just contribute?" The answer is that [I initially did contribute to dbutils](http://commons.apache.org/proper/commons-dbutils/team-list.html#wspeirs), but have since stopped because of [how](http://mail-archives.apache.org/mod_mbox/commons-dev/201112.mbox/%3CCAB_f4-W2A-SOLuDqKtMc7726iSxL3_1UyAWovrunEXyOAp13LQ@mail.gmail.com%3E) [hard](http://mail-archives.apache.org/mod_mbox/commons-dev/201112.mbox/%3CCAOGo0VaET4kb6_Krugg7kMK2vML30dhZVUdQV-5M89NXNG6dGQ@mail.gmail.com%3E) it is to release new versions of commons-dbutils. Long story short, I gave up. However, I had already written version 2 of dbutils and wanted to make sure it saw the light of day. So instead of trying work through commons-dbutils and [all its problems](https://mail-archives.apache.org/mod_mbox/commons-dev/201310.mbox/%3CCAPVuaYtpLdj7yW7kRvKAx6BKOc00hW-CvGCui_RtFH+66YpBEQ@mail.gmail.com%3E) I simply decided to fork the project and "start over" with sop4j-dbutils.

What's new in sop4j-dbutils?
----------------------------

The biggest feature that sop4j-dbutils (or dbutilsv2) brings to the table is a fluent API with [named parameter support](https://issues.apache.org/jira/browse/DBUTILS-105). Other than that, a few bug fixes have been merged into the code and hopfully by hosting on GitHub anyone can make a pull request with a patch and/or file a bug.

How can I help?
---------------

Helping is easy, just clone the repo, make your changes, and file a pull request. If you don't feel comfortable making changes directly -- and you should-- you can also file a bug/issue.

If you like what you see here please check out the [SOP4J blog](http://www.sop4j.com).
