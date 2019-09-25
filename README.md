# PDGuard: Control and secure the processing of personal data

This repository contains the implementation of a
framework for the control and secure processing of personal data which we call PDGuard.
The full decription of this framework has been published by the
[International Journal of Information Security](https://rdcu.be/bRQqg).
A pre-print is also [available](https://dimitro.gr/assets/papers/MSKS19.pdf).

## Overview

Online personal data are rarely,
if ever,
effectively
controlled by the users they concern.
Worse,
as demonstrated
by the numerous daily leaks,
the organizations that
store and process them fail to adequately
safeguard the required confidentiality.
The PDGuard framework contained in this repository
defines,
prototypes,
and demonstrates an architecture and
an implementation that address both
of the aforementioned problems.
In the context of PDGuard,
personal data are always stored
encrypted as opaque objects.
Processing them can only be performed through
the PDGuard API,
under data and action-specific
authorizations supplied online by third-party agents.
Through these agents end-users can easily and reliably
authorize and audit how organizations use their personal data.
Thus,
PDGuard changes the problem
of personal data management from the,
apparently,
intractable problem of supervising processes,
operations,
personnel,
and a large software
stack to that of auditing for compliance the
applications that use the framework.
The applicability of
the PDGuard framework is demonstrated through
a stand-alone e-shop application and
through the integration of PDGuard into the identity application
used by *The Guardian* newspaper's website.


## PDGuard setup

To set up PDGuard, you have to install the following requirements:

### Java
PDGuard uses Java 8. You can install it as below.

```bash
sudo add-apt-repository ppa:webupd8team/java
sudo apt update
sudo apt install oracle-java8-installer
```

### Play

PDGuard uses the [Play framework](https://www.playframework.com/)
to port an instance of an escrow agent.

You can install and run Play by downloading [Activator](https://www.typesafe.com/activator/download)
Then, you add activator to you classpath as follows:

On Linux, use
```bash
export PATH=/path/to/activator:$PATH
```

## Escrow Agent

Escrow Agent consists of the three sub-projects listed below.

- common
- auth
- web

### common

Common sub-project includes common code used both by
the `auth` and `web` sub-projects.

### auth

This sub-project is responsible for the authorization flow
and the share of encryption keys to authenticated and authorized entities.

This is the component that data controller's application interact with
to get the encryption keys of data subject's personal data.

### web

This is an prototype implementation of Escrow Agent's web UI.
A data subject can log into this to set new authorization rules,
modify existing ones or review actions on their personal data by data controllers.

### Run Escrow Agent

You can run escrow agent's application as a whole but you can also run
each sub-project seperately.
(It is going to take a while the first time you run Escrow Agent).

#### Run the auth sub-project

```bash
activator
project auth
run
```

Now your app runs on [https://localhost:9443](https://localhost:9443)

#### Run web sub-project

```bash
activator
project web
run
```

Now your local web UI runs on [http://localhost:9000](http://localhost:9000)

## e-shop apllication

A mock e-shop application is implemented to visualize
and demonstrate the use of the escrow agent's encryption
and decryption abilities into a real application.

#### Run e-shop

E-shop application uses Play framework too. To run it, execute the following command

```bash
activator run
```

e-shop runs on [http://localhost:9043](http://localhost:9043)
