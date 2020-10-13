import csv
import os
import glob

folder = input("Enter folder: ")

for file in os.listdir("./" + folder):
    filepath = os.path.join(folder, file)
    lines = []
    with open(filepath, newline='') as csvfile:
        reader = csv.reader(csvfile)

        split_file = file.split("_")

        i = 0
        for row in reader:
            if i == 0:
                headers = row[0:5]
                i += 1
            elif i == 1:
                sensor_position = row[5]
                sensor_side = row[6]
                activity_type = split_file[1]
                activity_code = row[8]
                subject_id = row[7]

                lines.append(["# Sensor position: " + sensor_position])
                lines.append(["# Sensor side: " + sensor_side])
                lines.append(["# Activity type: " + activity_type])
                lines.append(["# Activity code: " + activity_code])
                lines.append(["# Subject id: " + subject_id])
                lines.append(headers)
                lines.append(row[0:5])
                i += 1
            else:
                lines.append(row[0:5])

    new_csv = subject_id + "_" + activity_type + "_" + sensor_position + "_" + sensor_side + "_" + split_file[2] 
    new_filepath = os.path.join(folder + "_new", new_csv)
    with open(new_filepath, 'w', newline='') as new_file:
        writer = csv.writer(new_file)
        for row in lines:
            writer.writerow(row)
