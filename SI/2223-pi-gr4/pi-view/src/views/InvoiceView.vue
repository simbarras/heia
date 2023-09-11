<template>
  <div>
    Information about the occurrence :
    <InvoiceOccurs/>
    <br>
    Information about the activity that took place :
    <InvoiceTakePlace/>
    <br>


  </div>
</template>

<script setup>
import InvoiceOccurs from "@/components/InvoiceOccurs.vue";
import InvoiceTakePlace from "@/components/InvoiceTakePlace.vue";
import InvoicePerson from "@/components/InvoicePerson.vue";
import InvoiceConsumable from "@/components/InvoiceConsumable.vue";

</script>
