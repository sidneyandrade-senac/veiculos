    async function getData(url) {
        try {
            const response = await fetch(url);
            if (!response.ok) {
            throw new Error(`Response status: ${response.status}`);
            }

            const resultado = await response.json();
            return resultado;
        } catch (error) {
            console.error(error.message);
        }
    }