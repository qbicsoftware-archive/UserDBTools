# UserDBTools

QBiC User DB Tools enables users to add people and affiliations to our mysql user database.
Copyright (C) 2016  Andreas Friedrich

There are currently two different access levels, controlled through Liferay User Groups:
The first has some basic functionality to add users and affiliations.
The admin group can additionally change head and contact persons of affiliations as well as connect multiple affiliations to one person as well as delete these connections.

Groups are defined in the file qbic-ext.properties (see other portlets), there has to be at least one group for each access level:

mysql.input.usergrp = name-of-user-grp[,other_group_with_same_rights,...]
mysql.input.admingrp = name-of-admin-user-grp[,other_group_with_same_rights,...]
