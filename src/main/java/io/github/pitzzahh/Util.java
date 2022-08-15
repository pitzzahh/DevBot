package io.github.pitzzahh;

public class Util {
    public static final String[] JOKES = {
            "V2h5IGRpZCB0aGUgc2NhcmVjcm93IHdpbiBhbiBhd2FyZA==?IEJlY2F1c2UgaGUgd2FzIG91dHN0YW5kaW5nIGluIGhpcyBmaWVsZC4=",
            "V2hhdCBkbyB5b3UgY2FsbCBhIHBpZyB0aGF0IGRvZXMga2FyYXRl?IEEgcG9yayBjaG9wLg==",
            "V2h5IGRpZCB0aGUgbWVsb24ganVtcCBpbnRvIHRoZSBsYWtl?IEl0IHdhbnRlZCB0byBiZSBhIHdhdGVyLW1lbG9uLg==",
            "V2h5IGRpZCB0aGUgZHJ1bSB0YWtlIGEgbmFw?IEl0IHdhcyBiZWF0Lg==",
            "V2hhdCBkbyB5b3UgY2FsbCB0d28gbW9ua2V5cyB0aGF0IHNoYXJlIGFuIEFtYXpvbiBhY2NvdW50?IFByaW1lIG1hdGVzLg==",
            "V2h5IGRpZCB0aGUgdHJlZSBnbyB0byB0aGUgZGVudGlzdA==?IEl0IG5lZWRlZCBhIHJvb3QgY2FuYWwu",
            "V2hlcmUgZG8gY293cyBnbyBmb3IgZW50ZXJ0YWlubWVudA==?IFRoZSBtb29vb28tdmllcyE=",
            "V2hhdOKAmXMgYW4gYXN0cm9uYXV04oCZcyBmYXZvcml0ZSBjYW5keQ==?IEEgTWFycyBiYXIu==",
            "V2hlcmUgZG8gc2hlZXAgZ28gdG8gZ2V0IHRoZWlyIGhhaXIgY3V0?IFRoZSBiYWEtYmFhIHNob3Au",
            "SG93IGRvIHlvdSBrbm93IHdoZW4gdGhlIG1vb24gaGFzIGhhZCBlbm91Z2ggdG8gZWF0?IFdoZW4gaXTigJlzIGZ1bGwu",
            "V2hhdCBraW5kIG9mIG11c2ljIGRvIHBsYW5ldHMgbGlrZQ==?IE5lcHR1bmVzLg==",
            "SG93IGRvIHlvdSB0ZWxsIGlmIGEgdmFtcGlyZSBpcyBzaWNr?IEJ5IGhvdyBtdWNoIGhlIGlzIGNvZmZpbi4=",
            "V2hhdCBpcyB0aGUgZGlmZmVyZW5jZSBiZXR3ZWVuIGEgdGVhY2hlciBhbmQgYSB0cmFpbg==?IE9uZSBzYXlzLCAiU3BpdCBvdXQgeW91ciBndW0sIiBhbmQgdGhlIG90aGVyIHNheXMsICJDaG9vIGNob28gY2hvbyIu",
            "V2h5IGRvZXMgSHVtcHR5IER1bXB0eSBsb3ZlIGF1dHVtbg==?IEJlY2F1c2UgSHVtcHR5IER1bXB0eSBoYWQgYSBncmVhdCBmYWxsLg==",
            "SG93IGRvIHRyZWVzIGFjY2VzcyB0aGUgaW50ZXJuZXQ=?IFRoZXkgbG9nIGluLg==",
            "V2h5IGlzIGl0IHNhZCB0aGF0IHBhcmFsbGVsIGxpbmVzIGhhdmUgc28gbXVjaCBpbiBjb21tb24=?IEJlY2F1c2UgdGhleeKAmWxsIG5ldmVyIG1lZXQu",
            "V2h5IGFyZSBvYnR1c2UgYW5nbGVzIHNvIGRlcHJlc3NlZA==?IEJlY2F1c2UgdGhleeKAmXJlIG5ldmVyIHJpZ2h0Lg==",
            "V2h5IGRpZCB0aGUgYmVlIGdldCBtYXJyaWVk?IEhlIGZvdW5kIGhpcyBob25leS4=",
            "V2hhdCBkbyB5b3UgY2FsbCBhIGZha2Ugbm9vZGxl?IEFuIGltcGFzdGEu",
            "V2hhdCBkb2VzIGl0IG1ha2UgeW91IGlmIHlvdSBzZWUgYSByb2JiZXJ5IGF0IGFuIEFwcGxlIFN0b3Jl?IEFuIGl3aXRuZXNzLg==",
            "V2hhdCBpcyBhbiBhc3Ryb25hdXTigJlzIGZhdm9yaXRlIGtleSBvbiBhIGtleWJvYXJk?IFRoZSBzcGFjZSBiYXIu",
            "Q2FuIEZlYnJ1YXJ5IE1hcmNo?IE5vIGJ1dCBBcHJpbCBNYXk=",
            "SG93IGRvIGNvd3Mgc3RheSB1cCB0byBkYXRl?IFRoZXkgcmVhZCB0aGUgTW9vLXNwYXBlci4=",
            "V2hhdCdzIHRoZSBkaWZmZXJlbmNlIGJldHdlZW4gYSB3ZWxsLWRyZXNzZWQgbWFuIG9uIGEgdW5pY3ljbGUgYW5kIGEgcG9vcmx5LWRyZXNzZWQgbWFuIG9uIGEgYmljeWNsZQ==?IEF0dGlyZQ==",
            "V2hlcmUgZG8gcGlyYXRlcyBnZXQgdGhlaXIgaG9va3M=?IFNlY29uZCBoYW5kIHN0b3Jlcy4=",
            "V2hhdOKAmXMgdGhlIG1vc3QgbXVzaWNhbCBwYXJ0IG9mIHRoZSBjaGlja2Vu?IFRoZSBkcnVtc3RpY2su",
            "V2h5IHdhcyB0aGUgbWF0aCBib29rIHNhZA==?IEJlY2F1c2UgaXQgaGFkIHNvIG1hbnkgcHJvYmxlbXMu",
            "V2h5IHdhcyBzaXggc2NhcmVkIG9mIHNldmVu?IEJlY2F1c2Ugc2V2ZW4gImF0ZSIgbmluZS4=",
            "RGlkIHlvdSBoZWFyIHRoZSBydW1vciBhYm91dCB0aGUgYnV0dGVy?IE5ldmVyIG1pbmQsIEkgc2hvdWxkbuKAmXQgc3ByZWFkIGl0IQ==",
            "V2h5IGNhbuKAmXQgYSBiaWN5Y2xlIHN0YW5kIG9uIGl04oCZcyBvd24=?IEl0IGlzIHR3byB0aXJlZC4=",
            "V2hhdCBicmFuZCBvZiB1bmRlcndlYXIgZG8gc2NpZW50aXN0cyB3ZWFy?IEtlbHZpbiBLbGVpbi4=",
            "V2hpY2ggZGF5cyBhcmUgdGhlIHN0cm9uZ2VzdA==?IFNhdHVyZGF5IGFuZCBTdW5kYXksIHRoZSByZXN0IGFyZSB3ZWVrZGF5cy4=",
            "V2hhdCdzIGJsdWUgYW5kIG5vdCB2ZXJ5IGhlYXZ5?IExpZ2h0IGJsdWUu",
            "V2hhdCBkbyB5b3UgY2FsbCBhIHNhZCBjdXAgb2YgY29mZmVl?IERlcHJlc3NvLg==",
            "V2hhdOKAmXMgZ3JlZW4gYW5kIGhhcyB3aGVlbHM=?IEdyYXNzLiBJIGxpZWQgYWJvdXQgdGhlIHdoZWVscy4="
    };

    /**
     * Gets a delay based on the length of the joke.
     * @param question the quesiton joke.
     * @return an {@code int} the lenght of the delay.
     */
    public static int getDelay(final String question) {
        switch (question.length()) {
            case 10,11,12,13,14 -> {
                return 500;
            }
            case 15,16,17,18,19 -> {
                return 1000;
            }
            case 20,21,22,23,24 -> {
                return 1500;
            }
            case 25,26,27,28,29 -> {
                return 2000;
            }
            case 30,31,32,33,34 -> {
                return 2500;
            }
            case 35,36,37,38,39 -> {
                return 3000;
            }
            case 40,41,42,43,44 -> {
                return 3500;
            }
            case 45,46,47,48,49 -> {
                return 4000;
            }
            case 50,51,52,53,54,55,56,57,58,59 -> {
                return 4500;
            }
        }
        return 5000;
    }
}
