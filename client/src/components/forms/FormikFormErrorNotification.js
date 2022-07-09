import {useFormikContext} from 'formik'
import {useEffect} from 'react'
import swal from "sweetalert";

const FormikFormErrorNotification = () => {
    const { isValid, isValidating, isSubmitting } = useFormikContext()

    useEffect(() => {
        if (!isValid && !isValidating && isSubmitting) {
            swal("Looks like you forgot something...",
                "Check that all of the required inputs have been filled and then try again.",
                "warning");
        }
    }, [isSubmitting, isValid, isValidating])

    return null
}

export default FormikFormErrorNotification