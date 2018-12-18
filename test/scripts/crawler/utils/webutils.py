# -*- coding: utf-8 -*-
import requests

def requestPage(url, charset = False, max_trials = 5):
    page = None

    resp = None
    while max_trials > 0:
        try:
            print(str(max_trials) + ': ' + url)
            resp = requests.get(url, timeout=(3, 5))
            break
        except requests.exceptions.Timeout:
            max_trials -= 1
            print('Try to open ' + url + ' failed...')
        except requests.exceptions.RequestException as e:
            print(e)
            break

    if resp is None:
        return ''

    resp.encoding = 'utf-8'
    page = resp.text

    if charset:
        resp_charset = None
        tmp = re.search('charset=["\']?([-a-zA-Z0-9]+)["\']?', resp.text)
        if tmp:
            resp_charset = tmp.group(1)
            if resp_charset.startswith('gb'):
                page = resp.content.decode('gb18030')

    if page is None:
        return ''
    else:
        return page
