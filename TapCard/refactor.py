import os
import shutil
import glob

base = "app/src/main/java/com/tapcard/app"

moves = [
    (f"{base}/database", f"{base}/data/local"),
    (f"{base}/data/models", f"{base}/domain/model"),
    (f"{base}/repository/LocalProfileRepositoryImpl.kt", f"{base}/data/repository/LocalProfileRepositoryImpl.kt"),
    (f"{base}/repository/ProfileRepository.kt", f"{base}/domain/repository/ProfileRepository.kt"),
    (f"{base}/viewmodel", f"{base}/ui/viewmodel")
]

# Ensure dirs exist and move
for src, dst in moves:
    if os.path.isfile(src):
        os.makedirs(os.path.dirname(dst), exist_ok=True)
        shutil.move(src, dst)
    elif os.path.isdir(src):
        os.makedirs(os.path.dirname(dst), exist_ok=True)
        shutil.move(src, dst)

# Remove old dirs if empty
for d in [f"{base}/repository"]:
    if os.path.exists(d) and not os.listdir(d):
        os.rmdir(d)

# Replacements (old_package, new_package)
replacements = [
    ("com.tapcard.app.database", "com.tapcard.app.data.local"),
    ("com.tapcard.app.data.models", "com.tapcard.app.domain.model"),
    ("com.tapcard.app.repository", "com.tapcard.app.domain.repository"), # mostly interfaces are referenced this way
    ("com.tapcard.app.viewmodel", "com.tapcard.app.ui.viewmodel"),
]

# Walk all kt files and apply package/import replacements
for root, dirs, files in os.walk(base):
    for f in files:
        if f.endswith(".kt"):
            path = os.path.join(root, f)
            with open(path, "r") as file:
                content = file.read()
                
            # Update imports and package declarations
            for old_pkg, new_pkg in replacements:
                content = content.replace(old_pkg, new_pkg)
                
            # Special case for LocalProfileRepositoryImpl which changed to data.repository
            if f == "LocalProfileRepositoryImpl.kt":
                content = content.replace("package com.tapcard.app.domain.repository", "package com.tapcard.app.data.repository")
            
            # Special case: MainActivity importing LocalProfileRepositoryImpl needs new import
            if f == "MainActivity.kt":
                content = content.replace("import com.tapcard.app.domain.repository.LocalProfileRepositoryImpl", "import com.tapcard.app.data.repository.LocalProfileRepositoryImpl")

            with open(path, "w") as file:
                file.write(content)

print("Refactoring complete.")
