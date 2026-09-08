# Gitlet Design Document

**Name**:

## Classes and Data Structures

### Class 1

#### Fields

1. Field 1
2. Field 2


### Class 2

#### Fields

1. Field 1
2. Field 2


## Algorithms

## Persistence
The .gitlet directory structure:

.gitlet/
├── blobs/ -- stores the actual contents of the files, without their filenames (filenames are stored in commits for files in working directory and staging for files in staging area)
│   ├── 0379c986d1d704b1d13fb3d4e8cd90320dd0844e -- filename is SHA-1 of contents of file
│   └── da115e0c998511a7f0c9996bb7f88debbda1ce83
├── commits/
│   ├── 3f19369aad68cf94c5b27a4934f69d34b59c4304 -- filename is SHA-1 of the commit
│   └── ca1e243ff0ef31141246d06b2139f6fe394a0c5c
├── HEAD -- the current branch of working directory, contents is initially "master"
├── refs -- a simple Map (not Class) recording the commit that each branch points to
└── staging -- staging area status, i.e. files staged for addition (modification) and removal
