<img align="right" src="https://raw.githubusercontent.com/enonic/xp/master/misc/logo.png">

# Enonic XP

[![Build Status](https://travis-ci.org/enonic/xp.svg?branch=master)](https://travis-ci.org/enonic/xp)
[![Codecov](https://codecov.io/gh/enonic/xp/branch/master/graph/badge.svg)](https://codecov.io/gh/enonic/xp)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/ceca6f602c2a43e7a2f32287e202fe2c)](https://www.codacy.com/app/enonic/xp?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=enonic/xp&amp;utm_campaign=Badge_Grade)

Welcome to the home of Enonic XP. Here you will find all source code for the product. To get started,
please read our docs here: http://xp.readthedocs.org.

## Building

Before trying to build the project, you need to verify that the following software are installed:

* Java 8 (update 92 or above) for building and running.
* Gradle 2.x build system.
* Git installed on system.

Build all code and run all tests including integration tests:

    gradle build

Build all code skipping all tests:

    gradle build -x test

## Running

After building the project, you can start it locally by running the server script (or `bat` on Windows):

    modules/distro/build/install/bin/server.sh
    
You can also go to the `modules/distro` directory and type:
    
    gradle run    

## License

This software is licensed under AGPL 3.0 license. See full license terms [here](http://www.enonic.com/license). Also the distribution includes
3rd party software components. The vast majority of these libraries are licensed under Apache 2.0. For a complete list please
read [NOTICE.txt](https://github.com/enonic/xp/raw/master/NOTICE.txt).

	Enonic XP
	Copyright (C) 2000-2017 Enonic AS.

	This program is free software: you can redistribute it and/or modify
	it under the terms of the GNU Affero General Public License as
	published by the Free Software Foundation, either version 3 of the
	License, or (at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU Affero General Public License for more details.

	You should have received a copy of the GNU Affero General Public License
	along with this program.  If not, see <http://www.gnu.org/licenses/>.
