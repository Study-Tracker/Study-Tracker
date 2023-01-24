/*
 * Copyright 2019-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import {useFormikContext} from 'formik';
import {useEffect} from 'react';
import swal from "sweetalert";

const FormikFormErrorNotification = () => {
    const { isSubmitting, errors, submitCount, values } = useFormikContext()

    useEffect(() => {
        if (submitCount > 0 && !isSubmitting && Object.keys(errors).length > 0) {
            let err = "";
            Object.entries(errors).forEach(([key, value]) => {
               err = err + "- " + value + "\n";
            });
            console.debug("Form values", values);
            console.debug("Validation errors", errors);
            swal("Looks like you forgot something...",
                "Check that all of the required inputs have been filled and then try again.\n\nValidation errors:\n" + err,
                "warning");
        }
    }, [submitCount, isSubmitting])

    return null
}

export default FormikFormErrorNotification