# CoMaS-Carleton
CoMaS client setup, no eula, no tos. Can't say it's illegal to redistribute if there's nothing saying against it.


From what I've looked into it so far CoMaS-Launcher-0.7.5, downloads a exam.ini file and a login.ini file, then downloads CoMaS-0.7.4g...

which is in login.ini (line 127), version=0.7.4g


... more later


# Capabilities (Exam Invigilator/E Proctor)

- VM Detection (pretty loose/relatively easy vm detection bypass)
- Windows Registry Checking (Specifically to look for your Documents & your Desktop folder of the current user)
- Random screenshots/webcam captures (Active computer, stores them on your desktop in a "CoMaS" folder iirc)
- All wifi/ethernet/bluetooth activity on your computer (with some of the dumbest checks to see if it's illegal activity or not.)
- Checks clipboard activity periodically.
- Collects hardware details. (Such as your mac address)
- Collects background resource usage stats
- Enable audio/video monitoring (Which is configurable to be optional or mandatory)

It does NOT check browser data
And it's only running when you open it to do the exam. It's not a rootkit, you monkey.



# Validation && Extra rambling/proof
Windows registry checking --->

(Launcher "utility\WindowsRegistry.class" accesses two values from your current logged in user, one of them is your Documents folder, the other is your Desktop)
"HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Shell Folders", "Personal");

"HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Shell Folders", "Desktop");

VM Detection ---> 
CoMaS-0.7.4g inside "resources"\VMCheck.class and VMCheckTask.class

