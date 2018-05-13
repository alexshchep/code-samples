# From Hackerrank.com
# Validating Email Addresses With a Filter
# main and filter_mail was given
# fun written by Alex Shchepetkin 
#
#    Valid email addresses must follow these rules:
#
#    It must have the username@websitename.extension format type.
#    The username can only contain letters, digits, dashes and #underscores.
#    The website name can only have letters and digits.
#    The maximum length of the extension is . 

import string
def fun(s):
    # return True if s is a valid email, else return False
    splitted = s.split('@')
    # split the username
    username = splitted[0]
    if len(username) == 0:
        return False
    if len(splitted[1:]) != 1:
        return False
    therest = ''.join(splitted[1:])
    # check if username is valid
    allowed = set(string.ascii_letters + string.digits + '-' + '_')
    if not set(username).issubset(allowed):
        # bad username
        return False
    
    # split the website from extension
    splitted = therest.split('.')
    website = splitted[0]
    if len(splitted[1:]) != 1:
        return False
    extension = ''.join(splitted[1:])
    
    # check if website name is valid
    allowed = set(string.ascii_letters + string.digits)
    if not set(website).issubset(allowed):
        # bad website name
        return False
    # check if extension has length of 3 or less
    if len(extension) > 3 or len(extension) < 1:
        return False
    # check if extension is valid
    if not set(extension).issubset(allowed):
        # bad website name
        return False    
    
    return True
def filter_mail(emails):
    return filter(fun, emails)

if __name__ == '__main__':
    n = int(raw_input())
    emails = []
    for _ in range(n):
        emails.append(raw_input())

    filtered_emails = filter_mail(emails)
    filtered_emails.sort()
    print filtered_emails
