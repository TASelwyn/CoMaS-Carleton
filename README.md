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

# Links

Professor that seems to of made it:
https://carleton.ca/scs/people/tony-white/
- CoMaS server runs on cogerent, which is his domain.
- Windows executable has him as the publisher.
- Apparently some students have talked to him about CoMaS extensively.
- According to some students, a libray tony uses extensively, being Jersey REST Library in his web services coruse. It's also used a lot in CoMaS. Jersey REST is dying, old tech.

A list of links used by CoMaS. 
Main domain used: https://comas.cogerent.com:8443
Main Executables:
- https://comas.cogerent.com:8443/CMS/rest/exam/CoMaS-Launcher-0.7.5.jar
- https://comas.cogerent.com:8443/CMS/rest/exam/CoMaS-0.7.4g.jar


Some configuration files:
https://comas.cogerent.com:8443/CMS/rest/exam/exam.ini
https://comas.cogerent.com:8443/CMS/rest/exam/login.ini


Funny/stupid links
https://comas.cogerent.com:8443/COMP4601-Directory/login.html
Seriously.. comp4601 directory. Probably from copy pasting students work as a login page. lol.
https://comas.cogerent.com:8443/CMS/rest/tools//server.jade
Seems to be an admin panel, not entirely sure.






# Validation && Extra rambling/proof

Windows registry checking --->

(Launcher "utility\WindowsRegistry.class" accesses two values from your current logged in user, one of them is your Documents folder, the other is your Desktop)
"HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Shell Folders", "Personal");

"HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Shell Folders", "Desktop");

VM Detection ---> 
CoMaS-0.7.4g inside "resources"\VMCheck.class and VMCheckTask.class

