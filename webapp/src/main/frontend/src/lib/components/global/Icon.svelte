<script lang="ts">
// https://icon-sets.iconify.design/material-symbols/
import Language from 'virtual:icons/material-symbols/language';
import Mail from 'virtual:icons/material-symbols/mail';
import Person from 'virtual:icons/material-symbols/account-circle';
import Business from 'virtual:icons/material-symbols/store';
import Phone from 'virtual:icons/material-symbols/call';
import Notification from 'virtual:icons/material-symbols/notifications';
import Calendar from 'virtual:icons/material-symbols/calendar-today';
import Add from 'virtual:icons/material-symbols/add-2';
// Diccionario: clave → componente
export const icons: Record<string, any> = {
  language: Language,
  mail: Mail,
  person: Person,
  business: Business,
  phone: Phone,
  calendar: Calendar,
  notification: Notification,
  add: Add
};

function iconFor(name: string) {
  return icons[name] ?? icons["person"];
}

    let {name} = $props()
    const Icon = iconFor(name)
</script>

<Icon/>