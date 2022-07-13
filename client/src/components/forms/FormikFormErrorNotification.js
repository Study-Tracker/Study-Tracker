import {useFormikContext} from 'formik'
import {useEffect} from 'react'
import swal from "sweetalert";

const FormikFormErrorNotification = () => {
    const { isValid, isValidating, isSubmitting, errors, submitCount } = useFormikContext()

    useEffect(() => {
        if (submitCount > 0 && !isSubmitting && Object.keys(errors).length > 0) {
            let err = "";
            Object.entries(errors).forEach(([key, value]) => {
               err = err + "- " + value + "\n";
            });
            console.debug(errors);
            swal("Looks like you forgot something...",
                "Check that all of the required inputs have been filled and then try again.\n\nValidation errors:\n" + err,
                "warning");
        }
    }, [submitCount, isSubmitting])

    return null
}

export default FormikFormErrorNotification