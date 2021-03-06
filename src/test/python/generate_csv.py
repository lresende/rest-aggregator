#
# Copyright 2017 Luciano Resende
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

import os
import csv

headings = ['first_name','last_name','count']

filePath = os.path.join(".", 'generated_input.csv')
with open(filePath, 'w', newline='') as csvFile:
    csvWriter = csv.writer(csvFile, dialect='excel')
    csvWriter.writerow(headings)
    for row in range(0,1000000):
        if row % 2 == 0:
            csvWriter.writerow(['piers','smith', 1])
        else:
            csvWriter.writerow(['kristen','smith', 1])
