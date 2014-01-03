Maven DBTools Plugin
=================

Maven DBTools Plugin


Usage
-----
    <build>
        <plugins>
            <!--DBTools Generator example-->
            <plugin>
                <groupId>org.dbtools</groupId>
                <artifactId>maven-dbtools-plugin</artifactId>
                <version><latest version></version>
                <configuration>
                    <basePackageName>org.project.wsj.domain</basePackageName>
                    <schemaDir>${basedir}/src/main/resources/db</schemaDir>
                    <dbVendor>derby</dbVendor>
                    <useDateTime>true</useDateTime>
                    <springSupport>true</springSupport>
                </configuration>
            </plugin>
        </plugins>
    </build>


License
-------

    Copyright 2014 Jeff Campbell

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
