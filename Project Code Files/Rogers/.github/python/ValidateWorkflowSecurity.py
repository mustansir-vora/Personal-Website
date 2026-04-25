import os
import inspect

import common

def validate():
    common.log(__file__ + "->" + inspect.currentframe().f_code.co_name)
